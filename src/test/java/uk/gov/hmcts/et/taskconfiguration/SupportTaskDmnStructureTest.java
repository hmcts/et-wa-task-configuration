package uk.gov.hmcts.et.taskconfiguration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_INITIATION_ET_EW;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_INITIATION_ET_SCOTLAND;

class SupportTaskDmnStructureTest {
    private static final String ARRANGE_SUPPORT_EXPRESSION = "ARRANGE_SUPPORT_TASK_NAME != null";
    private static final Set<String> REVIEW_SUPPORT_EVENTS = Set.of(
        "\"SUBMIT_CASE_DRAFT\"",
        "\"UPDATE_CASE_SUBMITTED\"",
        "\"SUBMIT_ET3_FORM\"",
        "\"UPDATE_ET3_FORM\"",
        "\"requestSupport\"",
        "\"createFlag\""
    );
    private static final List<ReviewSupportInput> REVIEW_SUPPORT_INPUTS = List.of(
        new ReviewSupportInput(
            "Admin Support Task Required",
            "adminTaskRequired",
            "\"ReviewSupportRequestAdmin\""
        ),
        new ReviewSupportInput(
            "Legal Officer Support Task Required",
            "legalOfficerTaskRequired",
            "\"ReviewSupportRequestLegalOfficer\""
        ),
        new ReviewSupportInput(
            "Judge Support Task Required",
            "judgeTaskRequired",
            "\"ReviewSupportRequestJudge\""
        )
    );

    static Stream<Arguments> initiationDmnProvider() {
        return Stream.of(
            Arguments.of("England and Wales", WA_TASK_INITIATION_ET_EW),
            Arguments.of("Scotland", WA_TASK_INITIATION_ET_SCOTLAND)
        );
    }

    @ParameterizedTest(name = "{0} uses the Arrange Support boolean input")
    @MethodSource("initiationDmnProvider")
    void arrange_support_rules_use_dedicated_boolean_input(String jurisdiction, DmnDecisionTable dmn)
            throws Exception {
        Document document = parse(dmn);
        Element decisionTable = (Element) document.getElementsByTagName("decisionTable").item(0);
        List<Element> inputs = directChildren(decisionTable, "input");

        int arrangeSupportInputIndex = inputExpressionIndex(inputs);
        Element arrangeSupportInput = inputs.get(arrangeSupportInputIndex);
        Element inputExpression = directChildren(arrangeSupportInput, "inputExpression").getFirst();

        assertEquals("Arrange Support Task Required", arrangeSupportInput.getAttribute("label"));
        assertEquals("boolean", inputExpression.getAttribute("typeRef"));

        Map<String, String> expectedRules = Map.of(
            "\"createFlag\"", "true",
            "\"manageFlags\"", "true"
        );
        List<Element> arrangeSupportRules = directChildren(decisionTable, "rule").stream()
            .filter(rule -> "\"ArrangeSupport\"".equals(entryText(rule, "outputEntry", 0)))
            .toList();

        assertEquals(2, arrangeSupportRules.size(), jurisdiction + " must have two Arrange Support rules");
        int eventInputIndex = inputIndex(inputs);
        for (Element rule : arrangeSupportRules) {
            String event = entryText(rule, "inputEntry", eventInputIndex);
            assertEquals(expectedRules.get(event), entryText(rule, "inputEntry", arrangeSupportInputIndex));
        }

        directChildren(decisionTable, "rule").stream()
            .flatMap(rule -> directChildren(rule, "inputEntry").stream())
            .map(Element::getTextContent)
            .forEach(text -> assertFalse(
                text.contains("ARRANGE_SUPPORT_TASK_NAME"),
                jurisdiction + " event rules must not contain the Arrange Support expression"
            ));
    }

    @ParameterizedTest(name = "{0} uses the Review Support boolean inputs")
    @MethodSource("initiationDmnProvider")
    void review_support_rules_use_dedicated_boolean_inputs(String jurisdiction, DmnDecisionTable dmn)
            throws Exception {
        Document document = parse(dmn);
        Element decisionTable = (Element) document.getElementsByTagName("decisionTable").item(0);
        List<Element> inputs = directChildren(decisionTable, "input");
        List<Element> rules = directChildren(decisionTable, "rule");
        int eventInputIndex = inputIndex(inputs);

        Map<ReviewSupportInput, Integer> inputIndexes = REVIEW_SUPPORT_INPUTS.stream()
            .collect(java.util.stream.Collectors.toMap(
                input -> input,
                input -> reviewSupportInputIndex(inputs, input)
            ));

        for (ReviewSupportInput reviewInput : REVIEW_SUPPORT_INPUTS) {
            List<Element> taskRules = rules.stream()
                .filter(rule -> reviewInput.taskId().equals(entryText(rule, "outputEntry", 0)))
                .toList();

            assertEquals(
                REVIEW_SUPPORT_EVENTS,
                taskRules.stream()
                    .map(rule -> entryText(rule, "inputEntry", eventInputIndex))
                    .collect(java.util.stream.Collectors.toSet()),
                jurisdiction + " has incorrect events for " + reviewInput.taskId()
            );
            assertEquals(6, taskRules.size(), jurisdiction + " must have six rules for " + reviewInput.taskId());

            for (Element rule : taskRules) {
                for (ReviewSupportInput candidate : REVIEW_SUPPORT_INPUTS) {
                    String expectedValue = candidate.equals(reviewInput) ? "true" : "";
                    assertEquals(expectedValue, entryText(rule, "inputEntry", inputIndexes.get(candidate)));
                }
            }
        }

        rules.stream()
            .map(rule -> entryText(rule, "inputEntry", eventInputIndex))
            .forEach(text -> assertFalse(
                text.contains("supportTaskState"),
                jurisdiction + " event rules must not contain Review Support indicator expressions"
            ));
    }

    private static Document parse(DmnDecisionTable dmn) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream resource = classLoader.getResourceAsStream(dmn.getFileName());
        assertNotNull(resource, "Unable to find " + dmn.getFileName());

        try (InputStream inputStream = resource) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            return factory.newDocumentBuilder().parse(inputStream);
        }
    }

    private static int inputIndex(List<Element> inputs) {
        for (int index = 0; index < inputs.size(); index++) {
            if ("eventId".equals(inputs.get(index).getAttribute("camunda:inputVariable"))) {
                return index;
            }
        }
        throw new AssertionError("No input found with " + "camunda:inputVariable" + "=" + "eventId");
    }

    private static int inputExpressionIndex(List<Element> inputs) {
        for (int index = 0; index < inputs.size(); index++) {
            List<Element> expressions = directChildren(inputs.get(index), "inputExpression");
            if (!expressions.isEmpty()
                && ARRANGE_SUPPORT_EXPRESSION.equals(expressions.getFirst().getTextContent().trim())) {
                return index;
            }
        }
        throw new AssertionError("No input found for expression: " + ARRANGE_SUPPORT_EXPRESSION);
    }

    private static int reviewSupportInputIndex(List<Element> inputs, ReviewSupportInput reviewInput) {
        String expectedExpression = normalise(reviewSupportExpression(reviewInput.field()));
        for (int index = 0; index < inputs.size(); index++) {
            List<Element> expressions = directChildren(inputs.get(index), "inputExpression");
            if (!expressions.isEmpty()
                && expectedExpression.equals(normalise(expressions.getFirst().getTextContent()))) {
                Element input = inputs.get(index);
                assertEquals(reviewInput.label(), input.getAttribute("label"));
                assertEquals("boolean", expressions.getFirst().getAttribute("typeRef"));
                return index;
            }
        }
        throw new AssertionError("No input found for " + reviewInput.field());
    }

    private static String reviewSupportExpression(String field) {
        return "if(additionalData!=null"
            + "and additionalData.Data!=null"
            + "and additionalData.Data.supportTaskState!=null"
            + "and additionalData.Data.supportTaskState." + field + "!=null)"
            + "then additionalData.Data.supportTaskState." + field + "=\"Yes\""
            + "else false";
    }

    private static String normalise(String value) {
        return value.replaceAll("\\s+", "");
    }

    private static String entryText(Element rule, String entryName, int index) {
        return directChildren(rule, entryName).get(index).getTextContent().trim();
    }

    private static List<Element> directChildren(Element parent, String name) {
        NodeList nodes = parent.getChildNodes();
        List<Element> children = new ArrayList<>();
        for (int index = 0; index < nodes.getLength(); index++) {
            Node node = nodes.item(index);
            if (node instanceof Element element && name.equals(element.getTagName())) {
                children.add(element);
            }
        }
        return children;
    }

    private record ReviewSupportInput(String label, String field, String taskId) {
    }
}

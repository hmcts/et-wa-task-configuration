package uk.gov.hmcts.et.taskconfiguration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_COMPLETION_ET_EW;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_COMPLETION_ET_SCOTLAND;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_CONFIGURATION_ET_EW;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_CONFIGURATION_ET_SCOTLAND;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_INITIATION_ET_EW;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_INITIATION_ET_SCOTLAND;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_PERMISSIONS_ET_EW;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_PERMISSIONS_ET_SCOTLAND;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_TYPE_ET_EW;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_TYPE_ET_SCOTLAND;

class ReviewSupportTaskDmnConsistencyTest {
    private static final Pattern REVIEW_SUPPORT_TASK_ID = Pattern.compile("ReviewSupportRequest[A-Za-z0-9]+");

    static Stream<Arguments> jurisdictionDmnProvider() {
        return Stream.of(
            Arguments.of(
                "England and Wales",
                WA_TASK_INITIATION_ET_EW,
                List.of(
                    WA_TASK_TYPE_ET_EW,
                    WA_TASK_CONFIGURATION_ET_EW,
                    WA_TASK_PERMISSIONS_ET_EW,
                    WA_TASK_COMPLETION_ET_EW
                )
            ),
            Arguments.of(
                "Scotland",
                WA_TASK_INITIATION_ET_SCOTLAND,
                List.of(
                    WA_TASK_TYPE_ET_SCOTLAND,
                    WA_TASK_CONFIGURATION_ET_SCOTLAND,
                    WA_TASK_PERMISSIONS_ET_SCOTLAND,
                    WA_TASK_COMPLETION_ET_SCOTLAND
                )
            )
        );
    }

    @ParameterizedTest(name = "{0} Review Support task IDs are consistent")
    @MethodSource("jurisdictionDmnProvider")
    void initiated_review_support_tasks_exist_in_each_lifecycle_dmn(String jurisdiction,
                                                                    DmnDecisionTable initiationDmn,
                                                                    List<DmnDecisionTable> lifecycleDmns)
            throws Exception {
        Set<String> initiatedTaskIds = reviewSupportTaskIds(initiationDmn);
        assertFalse(initiatedTaskIds.isEmpty(), jurisdiction + " initiation DMN has no Review Support tasks");

        for (DmnDecisionTable lifecycleDmn : lifecycleDmns) {
            assertEquals(
                initiatedTaskIds,
                reviewSupportTaskIds(lifecycleDmn),
                () -> lifecycleDmn.getFileName() + " is inconsistent with " + initiationDmn.getFileName()
            );
        }
    }

    private static Set<String> reviewSupportTaskIds(DmnDecisionTable dmn) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(dmn.getFileName())) {
            assertNotNull(inputStream, "Unable to find " + dmn.getFileName());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            Document document = factory.newDocumentBuilder().parse(inputStream);

            Matcher matcher = REVIEW_SUPPORT_TASK_ID.matcher(document.getDocumentElement().getTextContent());
            Set<String> taskIds = new TreeSet<>();
            while (matcher.find()) {
                taskIds.add(matcher.group());
            }
            return taskIds;
        }
    }
}

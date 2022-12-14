#!/bin/bash
## Usage: ./idam-service-token.sh [microservice_name]
##
## Options:
##    - microservice_name: Name of the microservice. Default to `ccd_gw`.
##
## Returns a valid IDAM service token for the given microservice.

microservice="${1:-ccd_gw}"

curl --silent --show-error -X POST \
  -H "Content-Type: application/json" \
  -d '{"microservice":"'${microservice}'"}' \
  ${SERVICE_AUTH_PROVIDER_API_BASE_URL:-http://localhost:4502}/testing-support/lease

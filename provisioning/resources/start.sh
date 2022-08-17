#!/bin/bash

if [[ "${ONTOP_SPEC}" == "" ]]; then
 echo "Using the default specification method";
 ONTOP_LOOP=( 
    [0]=${ONTOP_ONTOLOGY_FILE} 
    [1]=${ONTOP_MAPPING_FILE} 
    [2]=${ONTOP_PROPERTIES_FILE} 
    [3]=${ONTOP_PORTAL_FILE} 
    [4]=${ONTOP_DEV_MODE} 
    [5]=${ONTOP_PORT} 
 );
 ONTOP_SPEC=( 
    [0]=${ONTOP_LOOP[*]} 
 );
fi

ENDPOINT_LENGTH=${#ONTOP_SPEC[@]}
if [[ ${ENDPOINT_LENGTH} -gt 0 ]]; then
  echo "starting ${ENDPOINT_LENGTH} endpoints.";
else
  echo "Incorrect start specification: ${ONTOP_SPEC}.";
  exit 1;
fi

ENDPOINT_NUMBER=1;
NUMBER='^[0-9]+$'
TOML='^.*toml$'
CORS="--cors-allowed-origins=${ONTOP_CORS_ALLOWED_ORIGINS}"

for ONTOP_ENDPOINT_VAR in "${ONTOP_SPEC[@]}"
do
  ONTOP_ENDPOINT=( ${ONTOP_ENDPOINT_VAR} )
  echo "Providing endpoint ${ENDPOINT_NUMBER} on port ${ONTOP_ENDPOINT[5]}";

  ONTOLOGY_FILE="--ontology=${ONTOP_ENDPOINT[0]}"
  MAPPING_FILE="--mapping=${ONTOP_ENDPOINT[1]}"
  PROPERTIES_FILE="--properties=${ONTOP_ENDPOINT[2]}"
  
  if [[ "${ONTOP_ENDPOINT[3]}" =~ ${TOML} ]]; then
    PORTAL_FILE="--portal ${ONTOP_ENDPOINT[3]}"
  else
    PORTAL_FILE=""
  fi

  if [ "${ONTOP_ENDPOINT[4]}" == "true" ]; then
    DEV_MODE="--dev"
  else
    DEV_MODE=""
  fi

  if [[ "${ONTOP_ENDPOINT[5]}" =~ ${NUMBER} ]]; then
    PORT="--port=${ONTOP_ENDPOINT[5]}"
  else
    PORT=""
  fi

  echo "Arguments: ${ONTOLOGY_FILE} ${MAPPING_FILE} ${PROPERTIES_FILE} ${PORTAL_FILE} ${DEV_MODE} ${PORT} ${CORS}";

  if [[ "${ENDPOINT_NUMBER}" == "${ENDPOINT_LENGTH}" ]]; then
    echo "Invoking last process";
    java -cp ./lib/*:./jdbc/* -Dlogback.configurationFile=file:./log/logback.xml \
        it.unibz.inf.ontop.cli.Ontop endpoint ${ONTOLOGY_FILE} ${MAPPING_FILE} \
        ${PROPERTIES_FILE} ${PORTAL_FILE} ${DEV_MODE} ${PORT} ${CORS};
  else 
    echo "Invoking intermediate process";
    java -cp ./lib/*:./jdbc/* -Dlogback.configurationFile=file:./log/logback.xml \
        it.unibz.inf.ontop.cli.Ontop endpoint ${ONTOLOGY_FILE} ${MAPPING_FILE} \
        ${PROPERTIES_FILE} ${PORTAL_FILE} ${DEV_MODE} ${PORT} ${CORS}&
  fi
  ((ENDPOINT_NUMBER++))
done


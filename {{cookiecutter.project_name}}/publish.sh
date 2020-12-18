#!/usr/bin/env bash

docker_v2="docker-registry-v2.ae.sda.corp.telstra.com"
application_base_image="${docker_v2}/o2a/node-centos:10"

nexus_token="${bamboo_NEXUS_DEPLOYMENT_TOKEN_PASSWORD}"

docker run --rm -i \
  -v $(pwd):/app \
  -e nexus_token=${nexus_token} \
  -w /app \
  ${application_base_image} sh -c " \
    mv ci.npmrc ~/.npmrc && \
    npm config set _auth ${nexus_token} && \
    npm publish --access=public"

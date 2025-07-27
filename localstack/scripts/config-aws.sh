#!/bin/bash

echo "===> [Config] Configurando AWS CLI para LocalStack..."

AWS_ENDPOINT="http://s3.localhost.localstack.cloud:4566"
AWS_ACCESS_KEY_ID="test"
AWS_SECRET_ACCESS_KEY="test"

# Configura las credenciales
export AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
export AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}

# Configura el endpoint de S3 en LocalStack
awslocal configure set aws_access_key_id ${AWS_ACCESS_KEY_ID}
awslocal configure set aws_secret_access_key ${AWS_SECRET_ACCESS_KEY}
awslocal configure set region us-east-1
awslocal configure set s3.endpoint_url ${AWS_ENDPOINT}
awslocal configure set default.s3.addressing_style virtual  # Virtual-hosted addressing

echo "===> [Config] AWS CLI configurado correctamente."
echo "===> [Config] Endpoint: ${AWS_ENDPOINT}"

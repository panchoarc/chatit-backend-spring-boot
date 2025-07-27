#!/bin/bash

echo "===> [S3] Creando bucket en LocalStack..."

# Configuración
BUCKET_NAME="ecommerce-buyit-bucket"
AWS_ENDPOINT="http://s3.localhost.localstack.cloud:4566"

# Crear bucket
awslocal --endpoint-url=${AWS_ENDPOINT} s3api create-bucket --bucket ${BUCKET_NAME}

# Configurar CORS
echo "===> [S3] Configurando CORS para el bucket..."
awslocal --endpoint-url=${AWS_ENDPOINT} s3api put-bucket-cors \
  --bucket ${BUCKET_NAME} \
  --cors-configuration file:///etc/localstack/init/ready.d/cors.json
echo "===> [S3] CORS configurado correctamente."

# Listar buckets
echo "===> [S3] Buckets existentes:"
aws --endpoint-url=${AWS_ENDPOINT} s3api list-buckets

# Mostrar URL
echo "===> [S3] Bucket disponible en:"
echo "http://${BUCKET_NAME}.s3.localhost.localstack.cloud:4566"

#!/usr/bin/env bash
echo "Generating RSA key pair ..."
echo "1024 RSA key: private_key.pem"
openssl genrsa -out private_key.pem 1024

echo "create certification require file: rsaCertReq.csr"
openssl req -new -key private_key.pem -out rsaCertReq.csr

echo "create certification using x509: rsaCert.crt"
openssl x509 -req -days 3650 -in rsaCertReq.csr -signkey private_key.pem -out rsaCert.crt

echo "create public_key.der For IOS"
openssl x509 -outform der -in rsaCert.crt -out public_key.der

echo "create private_key.p12 For IOS. Please remember your password. The password will be used in iOS."
openssl pkcs12 -export -out private_key.p12 -inkey private_key.pem -in rsaCert.crt

echo "create rsa_public_key.pem For Java"
openssl rsa -in private_key.pem -out rsa_public_key.pem -pubout
echo "create pkcs8_private_key.pem For Java"
openssl pkcs8 -topk8 -in private_key.pem -out pkcs8_private_key.pem -nocrypt

echo "finished."
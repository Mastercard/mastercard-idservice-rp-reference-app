# Claims Identity Attributes Response

```json
{
  "transactionId": "1ec14310-e85c-11ea-adc1-0242ac120002",
  "verifiableCredential": {
    "@context": [ "https://www.w3.org/2018/credentials/v1", "https://www.w3.org/2018/credentials/id/v1" ],
    "type": [ "VerifiableCredential", "ID" ],
    "issuer": "https://idservice.com/",
    "issuanceDate": "2021-01-01T13:23:33Z",
    "credentialSubject": {
      "id": "0a58579bce2c6c4a9e413acaa7c4d53f9002ee69c736626dc9b48db50c6eb8ac",
      "claimsAttributes": {
        "assuranceLevel": 1,
        "lastVerifiedDate": "2021-01-01T13:23:33Z",
        "values": { "scopeValueName": "Value of Scope" },
        "dataMatch": "FULL"
      }
    },
    "proof": { "jws": "eyJraWQiOiJVTklUX1RFU1RfS0VZX0FMSUFTIiwiYWxnIjoiUlMyNTYifQ.." }
  },
  "fraudDetectionMeta": {
    "score": {
      "value": 0,
      "band": "GREEN",
      "signals": [ "anonymous_ip" ]
    }
  }
}

```
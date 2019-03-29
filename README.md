# Build & deploy

`
mvn clean install 
`

`
mvn azure-functions:deploy
`

# Run locally

You need to have a local.settings.json with all needed values (see template bellow)
in order to run the function locally.

`
mvn azure-functions:run
`

# Connect to logs

`
az webapp log tail --name <functio name> --resource-group <resourcegroup>
`

# Template for local.settings.json

```
{
  "IsEncrypted": false,
  "Values": {
    "AzureWebJobsStorage": "",
    "FUNCTIONS_WORKER_RUNTIME": "java",
    "iceguard-iot_DOCUMENTDB": "",
    "storageAccount": "",
    "collection-name-COSMOS": ""
  }
}
```
PHC Rails
=========

Set up config/databasedotcom.yml as follows

```
host: login.salesforce.com        # Use test.salesforce.com for sandbox
client_secret: 1234567890         # This is the Consumer Secret from Salesforce
client_id: somebigidthinghere     # This is the Consumer Key from Salesforce
sobject_module: SFDC_Models       # See below for details on using modules
debugging: true                   # Can be useful while developing
username: me@mycompany.com
password: mypasswordplusmysecuritytoken
```

Link to how to get Salesforce data into app: https://developer.salesforce.com/page/Accessing_Salesforce_Data_From_Ruby

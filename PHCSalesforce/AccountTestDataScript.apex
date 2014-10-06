Account[] accounts = new List<Account>();
Account acc = new Account(FirstName = 'Howard', LastName = 'Chen', SS_Num__c = '000000000');
accounts.add(acc);
acc = new Account(FirstName = 'J', LastName = 'Doe', SS_Num__c = '123456789');
accounts.add(acc);
insert accounts;

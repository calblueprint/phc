Map<String, Schema.SObjectField> fieldMap = Schema.SObjectType.Account.fields.getMap();
for (String s : fieldMap.keySet()) {
    System.debug(s);
}


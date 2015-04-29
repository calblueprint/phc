namespace :sf do
  desc "Imports Accounts data from salesforce"
  task import: :environment do
    byebug
    env = "production"
    username = ENV["sf_username_" + env]
    password = ENV["sf_password"] + ENV["sf_security_token_" + env]

    # True => Sandbox
    # False => Production
    salesforce = SalesforceBulk::Api.new(username, password, true)

    fields = ["Id", "FirstName","LastName","SS_Num__c","Birthdate__c","Phone","PersonEmail","Gender__c","Identify_as_GLBT__c",
      "Race__c", "Primary_Language__c", "Foster_Care__c","Veteran__c","Housing_Status_New__c","How_long_have_you_been_homeless__c",
      "Where_do_you_usually_go_for_healthcare__c","Medical_Care_Other__c"]

    query  = "SELECT " + fields.join(", ") + " from Account"
    puts "Querying salesforce..."

    # NOTE: Should we remove filter names that are null?
    response = salesforce.query("Account", query)
    response.result.records.each do |attrs|
      #Rename ID to SF_ID so we don't overrate ActiveRecord's primary key
      attrs = attrs.to_hash
      attrs[:sf_id] = attrs.delete("Id")
      puts "Updating account #{attrs[:sf_id]}: #{attrs["FirstName"]} #{attrs["LastName"]}"
      Account.find_or_create_by(sf_id: attrs[:sf_id]) do |account|
        account.update(attrs)
      end
    end
  end

  desc "Exports new Accounts, EventRegistrations, and Services information to Salesforce"
  task export: :environment do
    sf_fields = ["FirstName","LastName","SS_Num__c","Birthdate__c","Phone","PersonEmail","Gender__c","Identify_as_GLBT__c",
      "Race__c", "Primary_Language__c", "Foster_Care__c","Veteran__c","Housing_Status_New__c","How_long_have_you_been_homeless__c",
      "Where_do_you_usually_go_for_healthcare__c","Medical_Care_Other__c"]

    accounts_to_update = [] # Contain a SF_ID field in every account
    accounts_to_create = []
    Account.find_new_accounts().each do |account|
      matches = []
      # Try to find match on SSN OR FirstName+LastName+Birthdate
      unless account.SS_Num__c.nil? || (account.SS_Num__c.length != 9)
        matches += Account.where.not(sf_id: nil).where(SS_Num__c: account.SS_Num__c)
      end
      unless account.FirstName.nil? || account.LastName.nil? || account.Birthdate__c.nil?
        matches += Account.where.not(sf_id:nil)
                          .where(FirstName: account.FirstName,LastName: account.LastName,Birthdate__c: Account.Birthdate__c)
      end

      # Use the Salesforce ID from the first match
      if matches.count > 0
        account.update(sf_id: matches[0][:sf_id])
        a = account.as_json.select { |k,v| sf_fields.includes?(k) }
        a["id"] = a.delete("sf_id")
        a.delete("created_at")
        a.delete("updated_at")
        accounts_to_update.push(a)
      else
      # Create the account if we couldn't find a matching salesforce ID
        a = account.as_json

        # REMOVE THIS BEFORE PROD!!!! AHHH
        a["FirstName"] = (a["FirstName"] || "New ") + "-- Updated"
        a.delete("id")
        a.delete("sf_id")
        a.delete("created_at")
        a.delete("updated_at")
        accounts_to_create.push(a)
      end
    end
    byebug
    env = "sandbox"
    username = ENV["sf_username_" + env]
    password = ENV["sf_password"] + ENV["sf_security_token_" + env]
    salesforce = SalesforceBulk::Api.new(username, password, true)

    result_create = salesforce.create("Account", accounts_to_create, true).result
    result_update = salesforce.update("Account", accounts_to_update, true).result
  end
end

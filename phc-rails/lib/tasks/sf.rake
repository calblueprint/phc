namespace :sf do
  desc "Imports Accounts data from salesforce"
  task import: :environment do
    byebug
    env = "production"
    username = ENV["sf_username_" + env]
    password = ENV["sf_password"] + ENV["sf_security_token_" + env]

    # True => Sandbox
    # False => Production
    if env == "production"
      salesforce = SalesforceBulk::Api.new(username, password, false)
    elsif
      salesforce = SalesforceBulk::Api.new(username, password, true)
    end

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

      # Try to find match on SSN OR FirstName+LastName+Birthdate
      matches = []
      unless account.SS_Num__c.nil? || (account.SS_Num__c.length != 9)
        matches += Account.where.not(sf_id: nil).where(SS_Num__c: account.SS_Num__c)
      end
      unless account.FirstName.nil? || account.LastName.nil? || account.Birthdate__c.nil?
        unless account.FirstName.empty?
          matches += Account.where.not(sf_id:nil)
                            .where(FirstName: account.FirstName, LastName: account.LastName, Birthdate__c: account.Birthdate__c)
        end
      end
      # Properly format birthday
      account.Birthdate__c = account.birthdate

      # Filter out nil fields, and select only Salesforce fields
      a = account.as_json.select { |k,v| sf_fields.include?(k) }

      # Use the Salesforce ID from the first match
      if matches.count > 0
        puts "Found #{matches.count} matche(s) for #{account.FirstName} #{account.LastName}"
        # account.update(sf_id: matches[0][:sf_id])
        a["id"] = matches[0][:sf_id]
        accounts_to_update.push(a)
      else
      # Create the account if we couldn't find a matching salesforce ID
        puts "Did not find any matche(s) for #{account.FirstName} #{account.LastName}"
        accounts_to_create.push(a)
      end
    end
    env = "sandbox"
    username = ENV["sf_username_" + env]
    password = ENV["sf_password"] + ENV["sf_security_token_" + env]

    if env == "production"
      salesforce = SalesforceBulk::Api.new(username, password, false)
    elsif
      salesforce = SalesforceBulk::Api.new(username, password, true)
    end

    def log_errors(result, data)
      if result.has_errors?
        File.open("errors.txt", "w+") do |f|
          result.errors.each do |error|
            i = error.keys()[0].to_i
            user = data[i]
            first, last = user["FirstName"], user["LastName"]
            message = error.values()[0]
            message = "Failed to upload: #{first} #{last}, because #{message} \n"
            puts message
            f.write(message)
          end
        end
      end
    end

    result_create = salesforce.create("Account", accounts_to_create[0..100], true).result
    result_update = salesforce.update("Account", accounts_to_update[0..100], true).result
    log_errors(result_create, accounts_to_create)
    log_errors(result_update, accounts_to_update)

    print "Done."
  end
end

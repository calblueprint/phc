namespace :sf do

  # task test_birthdate_export: :environment do
  #   salesforce = get_salesforce_session()

  #   test_account = Account.last
  #   sf_fields = ["FirstName","LastName","SS_Num__c","Birthdate__c","Phone","PersonEmail","Gender__c","Identify_as_GLBT__c",
  #     "Race__c", "Primary_Language__c", "Foster_Care__c","Veteran__c","Housing_Status_New__c","How_long_have_you_been_homeless__c",
  #     "Where_do_you_usually_go_for_healthcare__c","Medical_Care_Other__c"]
  #   salesforce_id = test_account.sf_id
  #   a = test_account.as_json.select { |k,v| sf_fields.include?(k) }

  #   a["id"] = salesforce_id

  #   a["Gender__c"] = "Male"
  #   a["FirstName"] = ""
  #   a["Birthdate__c"] = ""


  #   result = salesforce.update("Account", [a], true).result
  #   puts result.message

  #   byebug
  # end

  desc "Imports Accounts data from salesforce"
  task import: :environment do
    salesforce = get_salesforce_session()

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
  task export_accounts: :environment do
    sf_fields = ["FirstName","LastName","SS_Num__c","Birthdate__c","Phone","PersonEmail","Gender__c","Identify_as_GLBT__c",
      "Race__c", "Primary_Language__c", "Foster_Care__c","Veteran__c","Housing_Status_New__c","How_long_have_you_been_homeless__c",
      "Where_do_you_usually_go_for_healthcare__c","Medical_Care_Other__c"]

    accounts_to_update = [] # These accounts contain a SF_ID field, so we can match them
    accounts_to_create = []
    accounts_to_create_ids = [] # Save account id's so we can update the SF_ID after its created

    # Note: In the future, new accounts WILL have a SF_ID
    Account.find_new_accounts().each do |account|

      # Try to find match on SSN OR FirstName+LastName+Birthdate
      matches = []
      unless account.SS_Num__c.nil? || (account.SS_Num__c.length != 9)
        matches += Account.where.not(sf_id: nil).where(SS_Num__c: account.SS_Num__c)
      end
      unless account.FirstName.nil? || account.LastName.nil? || account.Birthdate__c.nil?
        unless account.FirstName.empty?
          matches += Account.where.not(sf_id:nil)
                            .where(FirstName: account.FirstName,
                                   LastName: account.LastName,
                                   Birthdate__c: account.Birthdate__c)
        end
      end

      # Properly format birthday
      account.Birthdate__c = account.birthdate

      # Filter out nil fields, and select only Salesforce fields
      a = account.as_json.select { |k,v| sf_fields.include?(k) }

      # Use the Salesforce ID from the first match
      if matches.count > 0
        puts "Found #{matches.count} match(es) for #{account.FirstName} #{account.LastName} -- will update."
        # Delete old record and update new one with SF_ID
        sf_id = matches[0][:sf_id]
        matches[0].delete()

        a["id"] = sf_id
        account.update(sf_id: sf_id)
        accounts_to_update.push(a)
      else
        # Create the account if we couldn't find a matching salesforce ID
        puts "Did not find any matches for #{account.FirstName} #{account.LastName} -- will create."
        accounts_to_create.push(a)
        accounts_to_create_ids.push(account.id)
      end
    end

    salesforce = get_salesforce_session()

    puts "--- Summary ---"
    if accounts_to_create.any?
      result_create = salesforce.create("Account", accounts_to_create, true).result

      # We need to map the newly created SF_IDs back to the Account objects
      result_create.records.each_with_index do |record, i|
        account = Account.find(accounts_to_create_ids[i])
        if record["Success"] == true
          sf_id = record["Id"]
          account.update(sf_id: sf_id)
        else
          error = record["Error"]
          puts "Error on #{accounts_to_create_ids[i]} #{account.to_hash()[:name]}: \n \t #{error}"
        end
      end

      log_errors(result_create, accounts_to_create)
      puts "Failed to create: #{result_create.errors.count} accounts."
      puts "Successfully created: #{accounts_to_create.count - result_create.errors.count} accounts."
    else
      puts "No accounts to create."
    end

    if accounts_to_update.any?
      result_update = salesforce.update("Account", accounts_to_update, true).result
      log_errors(result_update, accounts_to_update)
      puts "Failed to update: #{result_update.errors.count} accounts."
      puts "Successfully updated: #{accounts_to_update.count - result_update.errors.count} accounts."
    else
      puts "No accounts to update."
    end
 end

  task export_registrations: :environment do
    salesforce = get_salesforce_session()
    data = []
    EventRegistration.all.each do |reg|
      data.append(reg.to_salesforce_object)
    end
    result = salesforce.create("Event_Registration__c", data.compact, true).result
    puts "--- Summary ---"
    puts "Failed to create: #{result.errors.count} Event Registrations."
    puts "Successfully created: #{data.count - result.errors.count} Event Registrations."
    byebug
  end

  def log_errors(result, data)
    File.open("errors.txt", "a+") do |f|
      f.write("\n ----- Export on #{Time.now.strftime("%d/%m/%Y %H:%M")} ----- \n")
      result.errors.each do |error|
        i = error.keys()[0].to_i
        user = data[i]
        first, last = user["FirstName"], user["LastName"]
        message = error.values()[0]
        message = "Error on account #{first} #{last}: #{message} \n"
        f.write(message)
      end
      if result.success?
        puts "Salesforce returned success!"
      end
    end
  end

  def get_salesforce_session()
    env = ENV["sf_env"]
    username = ENV["sf_username_" + env]
    password = ENV["sf_password"] + ENV["sf_security_token_" + env]
    if env == "production"
      salesforce = SalesforceBulk::Api.new(username, password, false)
    elsif
      salesforce = SalesforceBulk::Api.new(username, password, true)
    end
    salesforce
  end

end

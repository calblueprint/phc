namespace :sf do

  desc "Imports Accounts data from salesforce"
  task import: :environment do
    salesforce = get_salesforce_session()

    fields = Account::FIELDS << "Id"
    fields.delete(:sf_id)

    query  = "SELECT " + fields.join(", ") + " from Account"
    puts "Querying salesforce..."

    # NOTE: Should we remove filter names that are null?
    response = salesforce.query("Account", query)
    response.result.records.each do |attrs|
      # Store Salesforce ID as "sf_id"
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
    accounts_to_update = []
    accounts_to_update_ids = []
    accounts_to_create = []
    accounts_to_create_ids = [] # Save account id's so we can update the SF_ID after its created

    Account.find_new_accounts().each do |account|
      # Properly format birthday
      account.Birthdate__c = account.birthdate

      # Filter out nil fields, and select only Salesforce fields
      a = account.as_json.select { |k,v| Account::FIELDS.include?(k) }

      # If the account doesn't have a Salesforce ID is empty, it needs to be created
      if account.sf_id.blank?
        accounts_to_create.push(a)
        accounts_to_create_ids.push(account.id)
      else
        a["id"] = account.sf_id
        accounts_to_update.push(a)
        accounts_to_update_ids.push(account.id)
      end
    end

    salesforce = get_salesforce_session()
    puts "--- Summary ---"
    if accounts_to_create.any?
      result_create = salesforce.create("Account", accounts_to_create, true).result

      # Map the newly created salesforce object id's back to the Account objects
      result_create.records.each_with_index do |record, i|
        account = Account.find(accounts_to_create_ids[i])
        if record["Success"] == true
          sf_id = record["Id"]

          # Update account salesforce id and mark as updated
          account.update(sf_id: sf_id)
          account.update(updated: true)
        else
          error = record["Error"]
          puts "Error on #{accounts_to_create_ids[i]} #{account.to_hash()[:name]}: \n \t #{error}"
        end
      end

      log_errors("account_create", result_create, accounts_to_create_ids)
      puts "Failed to create: #{result_create.errors.count} accounts."
      puts "Successfully created: #{accounts_to_create.count - result_create.errors.count} accounts."
    else
      puts "No accounts to create."
    end

    if accounts_to_update.any?
      result_update = salesforce.update("Account", accounts_to_update, true).result
      log_errors("account_update", result_update, accounts_to_update_ids)
      puts "Failed to update: #{result_update.errors.count} accounts."
      puts "Successfully updated: #{accounts_to_update.count - result_update.errors.count} accounts."
    else
      puts "No accounts to update."
    end
 end

  task export_registrations: :environment do
    salesforce = get_salesforce_session()
    data = []
    ids = []

    EventRegistration.all.each do |reg|
      # Skip Event Registrations that don't have a Salesforce account associated with them
      # This would happen if the account wasn't created because of an error
      if reg.account.sf_id.blank?
        next
      end
      data << reg.to_salesforce_object
      ids << reg.id
    end
    result = salesforce.create("Event_Registration__c", data.compact, true).result

    puts "--- Summary ---"
    puts result.message
    puts "Failed to create: #{result.errors.count} Event Registrations."
    puts "Successfully created: #{result.records.count - result.errors.count} Event Registrations."

    log_errors("event_reg", result, ids)
  end

  def log_errors(name, result, ids)
    failure = []
    result.records.each_with_index do |record, i|
      if !record["Success"]
        failure << data_id[i]
      end
    end
    export_yaml("data/#{name}_result", result)
    export_yaml("data/#{name}_failure_ids", failure)
  end

  def export_yaml(filename, data)
    File.open(filename, "a+") do |f|
      serialized = YAML::dump(data)
      f.write(serialized)
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

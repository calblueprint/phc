namespace :sf do
  desc "TODO"
  task import: :environment do
    byebug
    username = ENV["sf_username"]
    password = ENV["sf_password"] + ENV["sf_security_token"]

    # Make sure to remove the true argument in production.
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
      attrs[:sf_id] = attrs.delete "Id"

      puts "Updating account #{attrs[:sf_id]}: #{attrs["FirstName"]} #{attrs["LastName"]}"
      Account.find_or_create_by(sf_id: attrs[:sf_id]) do |account|
        account.update(attrs)
      end
    end

    # Retrieve the current PHC Event Info
    # query = "SELECT COUNT(*)

    Rake::Task[:import_sf].reenable
    #Rake::Task[:load_sf].invoke
  end

  desc "TODO"
  task export: :environment do
  end
end

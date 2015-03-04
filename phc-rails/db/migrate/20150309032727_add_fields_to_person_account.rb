class AddFieldsToPersonAccount < ActiveRecord::Migration
  def change
    add_column :person_accounts, :FirstName, :string
    add_column :person_accounts, :LastName, :string
    add_column :person_accounts, :SS_Num__c, :string
    add_column :person_accounts, :Birthdate__c, :string
    add_column :person_accounts, :Phone, :string
    add_column :person_accounts, :PersonEmail, :string
    add_column :person_accounts, :Gender__c, :string
    add_column :person_accounts, :Identify_as_GLBT__c, :boolean
    add_column :person_accounts, :Race__c, :string
    add_column :person_accounts, :Primary_Language__c, :string
    add_column :person_accounts, :Foster_care__c, :boolean
    add_column :person_accounts, :Veteran__c, :boolean
    add_column :person_accounts, :Housing_Status_New__c, :string
    add_column :person_accounts, :How_long_have_you_been_homeless__c, :string
    add_column :person_accounts, :Where_do_you_usually_go_for_healthcare__c, :string
    add_column :person_accounts, :Medical_Care_Other__c, :string

    remove_column :person_accounts, :first_name
    remove_column :person_accounts, :last_name
  end
end

class ChangeBirthdateToDateType < ActiveRecord::Migration
  def change
    remove_column :accounts, :Birthdate__c, :string
    add_column :accounts, :Birthdate__c, :date
  end
end

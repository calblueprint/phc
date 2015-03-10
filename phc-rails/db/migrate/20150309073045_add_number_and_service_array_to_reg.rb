class AddNumberAndServiceArrayToReg < ActiveRecord::Migration
  def change
    add_column :event_registrations, :Number__c, :string
  end
end

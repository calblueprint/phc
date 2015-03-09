class AddNumberAndServiceArrayToReg < ActiveRecord::Migration
  def change
    add_column :event_registrations, :Number__c, :string
    add_column :event_registrations, :services, :array
  end
end

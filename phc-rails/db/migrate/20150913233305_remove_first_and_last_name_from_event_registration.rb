class RemoveFirstAndLastNameFromEventRegistration < ActiveRecord::Migration
  def change
    remove_column :event_registrations, :FirstName, :string
    remove_column :event_registrations, :LastName, :string
  end
end

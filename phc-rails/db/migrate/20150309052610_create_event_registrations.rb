class CreateEventRegistrations < ActiveRecord::Migration
  def change
    create_table :event_registrations do |t|
      t.string :account_sfid
      t.string :phc_sfid
      t.string :FirstName
      t.string :LastName

      t.timestamps
    end
  end
end

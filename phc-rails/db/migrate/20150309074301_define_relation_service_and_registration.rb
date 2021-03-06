class DefineRelationServiceAndRegistration < ActiveRecord::Migration
  def change
    create_table :event_registrations_services, id: false do |t|
      t.integer :event_registration_id
      t.integer :service_id
    end

    add_index :event_registrations_services, [:event_registration_id, :service_id], \
      unique: true, name: 'join_table_index'
  end
end

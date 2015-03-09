class DefineRelationServiceAndRegistration < ActiveRecord::Migration
  def change
    create_table :event_registration_services, id: false do |t|
      t.belongs_to :event_registration, index: true
      t.belongs_to :service, index: true
    end

    add_index :event_registration_services, :event_registration
    add_index :event_registration_services, :service
  end
end

class FixAccountColumnName < ActiveRecord::Migration
  def change
    rename_column :event_registrations, :account_sfid, :account_id
  end
end

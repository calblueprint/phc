class RenamePersonAccount < ActiveRecord::Migration
  def change
    rename_table :person_accounts, :accounts
  end
end

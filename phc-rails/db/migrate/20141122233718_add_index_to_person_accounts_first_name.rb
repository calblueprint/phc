class AddIndexToPersonAccountsFirstName < ActiveRecord::Migration
  def change
      add_index :person_accounts, :first_name
      add_index :person_accounts, :last_name
  end
end

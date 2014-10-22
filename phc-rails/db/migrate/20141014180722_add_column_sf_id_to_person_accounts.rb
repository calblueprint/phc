class AddColumnSfIdToPersonAccounts < ActiveRecord::Migration
  def change
    add_column :person_accounts, :sf_id, :string
    add_index :person_accounts, :sf_id, unique: true
  end
end

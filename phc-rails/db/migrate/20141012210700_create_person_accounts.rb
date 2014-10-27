class CreatePersonAccounts < ActiveRecord::Migration
  def change
    create_table :person_accounts do |t|
      t.string :first_name
      t.string :last_name
      t.datetime :birthday

      t.timestamps
    end
  end
end

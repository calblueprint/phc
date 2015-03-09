class AddIndexForNamesAndRemoveBirthday < ActiveRecord::Migration
  def change
    remove_column :person_accounts, :birthday, :string

    add_index :person_accounts, :FirstName
    add_index :person_accounts, :LastName
  end
end

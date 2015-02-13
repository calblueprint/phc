class AddAuthDigestToUsers < ActiveRecord::Migration
  def change
    add_column :users, :auth_digest, :string
  end
end

class RemoveSfIdIndex < ActiveRecord::Migration
  def change
    remove_index "accounts", ["sf_id"]
  end
end

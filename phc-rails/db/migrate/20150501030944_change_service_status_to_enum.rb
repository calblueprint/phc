class ChangeServiceStatusToEnum < ActiveRecord::Migration
  def change
    remove_column :services, :status
    add_column :services, :status, :integer, null: false, default: 0
  end
end

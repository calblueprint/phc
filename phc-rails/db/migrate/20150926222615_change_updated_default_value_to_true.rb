class ChangeUpdatedDefaultValueToTrue < ActiveRecord::Migration
  def change
    change_column_default :accounts, :updated, true
  end
end

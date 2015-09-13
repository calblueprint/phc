class ChangeAccountUpdatedDefault < ActiveRecord::Migration
  def change
    change_column_null :accounts, :updated, false, false
    change_column_default :accounts, :updated, false
  end
end

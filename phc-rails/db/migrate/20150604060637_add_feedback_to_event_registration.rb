class AddFeedbackToEventRegistration < ActiveRecord::Migration
  def change
    add_column :event_registrations, :Experience__c, :integer
    add_column :event_registrations, :Services_Needed__c, :text
    add_column :event_registrations, :Feedback__c, :text
  end
end

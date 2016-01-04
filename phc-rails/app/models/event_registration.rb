# == Schema Information
#
# Table name: event_registrations
#
#  id                 :integer          not null, primary key
#  account_id         :string
#  phc_sfid           :string
#  created_at         :datetime
#  updated_at         :datetime
#  Number__c          :string
#  Experience__c      :integer
#  Services_Needed__c :text
#  Feedback__c        :text
#

class EventRegistration < ActiveRecord::Base
  has_and_belongs_to_many :services, :dependent => :destroy
  belongs_to :account

  serialize :Services_Needed__c

  def to_salesforce_object
    obj = {
           "Account__c" => self.account.sf_id,
           "PHC_Event__c" => ENV["phc_event_id"],
           "Experience__c" => self.Experience__c || "",
           "Services_Needed__c" => self.Services_Needed__c.join(", ") || "",
           "Feedback__c" => self.Feedback__c || ""
          }

    self.services.each do |service|
      obj[service.name] = service.status_string
    end

    obj
  end
end

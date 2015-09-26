# == Schema Information
#
# Table name: event_registrations
#
#  id                 :integer          not null, primary key
#  account_id         :string(255)
#  phc_sfid           :string(255)
#  created_at         :datetime
#  updated_at         :datetime
#  Number__c          :string(255)
#  Experience__c      :integer
#  Services_Needed__c :text
#  Feedback__c        :text
#

class EventRegistration < ActiveRecord::Base

  ##################################################
  # Associations
  ##################################################
  has_and_belongs_to_many :services
  belongs_to :account

  serialize :Services_Needed__c

  def self.phc_event
    # PHC 60 - PRODUCTION
    "a0R40000007lj0X"
  end

  def to_salesforce_object
    #"{"Account__c"=>"0014000001XUoNFAA1", "Acupuncture__c"=>"None", "PHC_Event__c"=>"a0R40000007HolJEAS"}"
    obj = {"Account__c" => self.account.sf_id,
           "PHC_Event__c" => EventRegistration.phc_event,
           "Experience__c" => self.Experience__c || "",
           "Services_Needed__c" => self.Services_Needed__c || "",
           "Feedback__c" => self.Feedback__c || "" }

    self.services.each do |service|
      obj[service.name] = service.status_string
    end

    obj
  end
end

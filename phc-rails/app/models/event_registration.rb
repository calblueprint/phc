# == Schema Information
#
# Table name: event_registrations
#
#  id                 :integer          not null, primary key
#  account_id         :string(255)
#  phc_sfid           :string(255)
#  FirstName          :string(255)
#  LastName           :string(255)
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
    # PHC 58 - PRODUCTION
    "a0R40000007kSvU"
  end

  def to_salesforce_object
    #"{"Account__c"=>"0014000001XUoNFAA1", "Acupuncture__c"=>"None", "PHC_Event__c"=>"a0R40000007HolJEAS"}"

    # TODO: We should rename account_sfid because it's confusing AF
    account = Account.where(id: self.account_sfid.to_i).first
    if account.nil?
      puts "Account #{self.account_sfid.to_i} has no associated account, skipping..."
      return
    end

    obj = {"Account__c" => account.sf_id, "PHC_Event__c" => EventRegistration.phc_event}
    self.services.each do |service|
      obj[service.name] = service.status
    end
    obj
  end
end

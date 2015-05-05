# == Schema Information
#
# Table name: event_registrations
#
#  id           :integer          not null, primary key
#  account_sfid :string(255)
#  phc_sfid     :string(255)
#  FirstName    :string(255)
#  LastName     :string(255)
#  created_at   :datetime
#  updated_at   :datetime
#  Number__c    :string(255)
#

class EventRegistration < ActiveRecord::Base
  has_and_belongs_to_many :services

  # TODO: Add a PHC Event Field!!
  # PHC SANDBOX EVENT: a0Re0000001hNY9
  def to_salesforce_object
    #"{"Account__c"=>"0014000001XUoNFAA1", "Acupuncture__c"=>"None", "PHC_Event__c"=>"a0R40000007HolJEAS"}"
    account = Account.where(id: self.account_sfid.to_i)
    services = services.map { |x| {x.name => x.status} }

    byebug
  end
end

# == Schema Information
#
# Table name: services
#
#  id         :integer          not null, primary key
#  name       :string(255)
#  created_at :datetime
#  updated_at :datetime
#

class Service < ActiveRecord::Base
  has_and_belongs_to_many :event_registrations

  # CONSTANTS for Service statuses
  def self.NONE
    "None"
  end

  def self.APPLIED
    "Applied"
  end

  def self.DROPIN
    "Drop In"
  end

  def self.RECIEVED
    "Received"
  end

  def self.services
    # In the future, we may retrieve these through the Salesforce API, but
    # for now they are hardcoded in
    ["Acupuncture__c", "Addiction_Recovery__c", "CAAP__c", "Dental__c", \
      "Disability_Services__c", "Employment__c", "Foodbank__c", \
      "Haircuts__c", "Legal__c", "Massage__c", "Medical__c", "Showers__c", \
      "Veteran_Services__c", "Wheelchair_Repair__c" ]
  end

end

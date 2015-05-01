# == Schema Information
#
# Table name: services
#
#  id         :integer          not null, primary key
#  name       :string(255)
#  created_at :datetime
#  updated_at :datetime
#  status     :string(255)
#

class Service < ActiveRecord::Base
  has_and_belongs_to_many :event_registrations

  enum :status, [:none, :applied, :drop_in, :received]

  def self.services
    # In the future, we may retrieve these through the Salesforce API, but
    # for now they are hardcoded in
    medical = ["Addiction_Recovery__c", "Dental__c", "HIV_STI_Testing__c", "Massage__c", \
               "Medical__c", "Mental_Health__c", "Needle_Exchange__c", "Podiatry__c", \
               "TB_Testing__c", "Vision_Readers__c", "Vision_Prescription__c"]
    support = ["Adult_Probation__c", "Banking__c", "Books__c", "CAAP__c", "CalFresh__c", \
               "Disability_Services__c", "DMV_ID__c", "Employment__c", "Family_Services__c", \
               "Foodbank__c", "Foot_Washing__c", "Haircuts__c", "Housing_Info__c", \
               "Legal__c", "Lunch__c", "Pet_Care__c", "Phone_Calls__c", "Senior_Services__c", \
               "Showers__c", "SSI_SSDI_Medi_Cal__c", "Veteran_Services__c", "Voicemail__c", \
               "Wheelchair_Repair__c", "Women_Services__c", "Youth_Services__c"]
    return medical + support
  end

end

# == Schema Information
#
# Table name: services
#
#  id         :integer          not null, primary key
#  name       :string(255)
#  created_at :datetime
#  updated_at :datetime
#  status     :integer          default(0), not null
#

class Service < ActiveRecord::Base

  enum status: [:unspecified, :applied, :drop_in, :received]

  has_and_belongs_to_many :event_registrations

  def self.services
    ServiceList.all.map(&:salesforce_name)
    # services = ["Acupuncture__c", "Addiction_Recovery__c", "Adult_Probation__c", "Banking__c", \
    #             "Books__c", "CAAP__c", "CalFresh__c", "Dental__c", "Disability_Services__c", \
    #             "DMV_ID__c", "Employment__c", "Family_Services__c", "Flu_Shot__c", "Foot_Washing__c", \
    #             "Foodbank__c", "Haircuts__c", "HIV_STI_Testing__c", "Homeward_Bound__c", \
    #             "Housing_Info__c", "Legal__c", "Lifeline_Cell__c", "Lunch__c", "Massage__c", \
    #             "Medical__c", "SSI_SSDI_Medi_Cal__c", "Mental_Health__c", "Needle_Exchange__c", \
    #             "Pet_Care__c", "Phone_Calls__c", "Photo_Portraits__c", "Vision_Readers__c", \
    #             "Senior_Services__c", "SSI_SSDI_Medi_Cal__c", "TB_Testing__c", "Veteran_Services__c", \
    #             "Vision_Screening__c", "Voter_Registration__c", "Wheelchair_Repair__c", "Youth_Services__c"]
  end

  def status_string
    case self.status
    when "unspecified"
      "None"
    when "applied"
      "Applied"
    when "drop_in"
      "Drop In"
    when "received"
      "Received"
    end
  end

end

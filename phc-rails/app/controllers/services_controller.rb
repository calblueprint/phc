class ServicesController < ApplicationController
  def show
      render :json => ["Acupuncture__c", "Addiction_Recovery__c", "CAAP__c", "Dental__c", \
        "Disability_Services__c", "Employment__c", "Foodbank__c", \
        "Haircuts__c", "Legal__c", "Massage__c", "Medical__c", "Showers__c", \
        "Veteran_Services__c", "Wheelchair_Repair__c" ]
  end
end

require 'rails_helper'

describe ServiceList, type: :model do
  it "saves a service" do
    ServiceList.delete_all
    service = ServiceList.create(name: "Food", salesforce_name: "Food__c")
    expect(ServiceList.find(service.id)).to eq(service)
  end
end

# == Schema Information
#
# Table name: service_lists
#
#  id              :integer          not null, primary key
#  name            :string
#  salesforce_name :string
#  created_at      :datetime         not null
#  updated_at      :datetime         not null
#

require 'rails_helper'

describe ServiceList, type: :model do
  it "saves a service" do
    ServiceList.delete_all
    service = ServiceList.create(name: "Food", salesforce_name: "Food__c")
    expect(ServiceList.find(service.id)).to eq(service)
  end

   describe "#services" do
    it "loads the services from service model" do
      ServiceList.delete_all
      ServiceList.create(salesforce_name: "a")
      ServiceList.create(salesforce_name: "b")
      ServiceList.create(salesforce_name: "c")
      expect(ServiceList.services).to eq(["a", "b", "c"])
    end
  end
end

require 'rails_helper'

describe Service, type: :model do
  describe "#services" do
    it "loads the services from service model" do
      ServiceList.delete_all
      ServiceList.create(salesforce_name: "a")
      ServiceList.create(salesforce_name: "b")
      ServiceList.create(salesforce_name: "c")
      expect(Service.services).to eq(["a", "b", "c"])
    end
  end

  describe "#status_string" do
    it "formats service into a string salesforce uses" do
      service = Service.new()
      service.applied!
      expect(service.status_string).to eq("Applied")
    end
  end
end

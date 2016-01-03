require 'rails_helper'

describe Service, type: :model do
  describe "#status_string" do
    it "formats service into a string salesforce uses" do
      service = Service.new()
      service.applied!
      expect(service.status_string).to eq("Applied")
    end
  end
end

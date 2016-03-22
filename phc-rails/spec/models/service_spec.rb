# == Schema Information
#
# Table name: services
#
#  id         :integer          not null, primary key
#  name       :string
#  created_at :datetime
#  updated_at :datetime
#  status     :integer          default(0), not null
#

require 'rails_helper'

describe Service, type: :model do
  describe "#status_string" do
    it "formats service into a string salesforce uses" do
      service = Service.new()
      service.applied!
      expect(service.status_string).to eq("Applied")
    end
  end

  it "belongs to a service in a service list" do
    service_list = ServiceList.create(name: "Interpretive Dance", salesforce_name: "interpretive_dance")
    service = service_list.services.create
    service.applied!
    expect(service.service_list).to eq(service_list.id)
  end
end

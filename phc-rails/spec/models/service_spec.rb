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
end

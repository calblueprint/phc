# == Schema Information
#
# Table name: event_registrations
#
#  id         :integer          not null, primary key
#  account_id :string(255)
#  phc_sfid   :string(255)
#  FirstName  :string(255)
#  LastName   :string(255)
#  created_at :datetime
#  updated_at :datetime
#  Number__c  :string(255)
#

require 'test_helper'

class EventRegistrationTest < ActiveSupport::TestCase
  # test "the truth" do
  #   assert true
  # end
end

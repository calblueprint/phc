# == Schema Information
#
# Table name: event_registrations
#
#  id           :integer          not null, primary key
#  account_sfid :string(255)
#  phc_sfid     :string(255)
#  FirstName    :string(255)
#  LastName     :string(255)
#  created_at   :datetime
#  updated_at   :datetime
#  Number__c    :string(255)
#

class EventRegistration < ActiveRecord::Base

  ##################################################
  # Associations
  ##################################################
  has_and_belongs_to_many :services

end

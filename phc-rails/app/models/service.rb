# == Schema Information
#
# Table name: services
#
#  id         :integer          not null, primary key
#  name       :string(255)
#  created_at :datetime
#  updated_at :datetime
#

class Service < ActiveRecord::Base
  has_and_belongs_to_many :event_registrations

  # CONSTANTS for Service statuses
  def self.NONE
    "None"
  end

  def self.APPLIED
    "Applied"
  end

  def self.DROPIN
    "Drop In"
  end

  def self.RECIEVED
    "Received"
  end

end
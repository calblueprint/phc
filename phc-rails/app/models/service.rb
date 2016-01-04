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

class Service < ActiveRecord::Base

  enum status: [:unspecified, :applied, :drop_in, :received]

  has_and_belongs_to_many :event_registrations

  def status_string
    case self.status
    when "unspecified"
      "None"
    when "applied"
      "Applied"
    when "drop_in"
      "Drop In"
    when "received"
      "Received"
    end
  end

end

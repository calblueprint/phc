# == Schema Information
#
# Table name: last_modifieds
#
#  id            :integer          not null, primary key
#  created_at    :datetime
#  updated_at    :datetime
#  last_modified :string(255)
#

class LastModified < ActiveRecord::Base
end

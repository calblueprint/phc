# == Schema Information
#
# Table name: users
#
#  id              :integer          not null, primary key
#  name            :string(255)
#  email           :string(255)
#  created_at      :datetime
#  updated_at      :datetime
#  password_digest :string(255)
#  auth_digest     :string(255)
#

class User < ActiveRecord::Base
  attr_accessor :auth_token
  has_secure_password
  VALID_EMAIL_REGEX = /\A[\w+\-.]+@[a-z\d\-.]+\.[a-z]+\z/i

  ##################################################
  # Validations
  ##################################################
  validates :name, presence: true, length: { maximum: 50 }
  validates :email, presence: true,
                    length: { maximum: 225 },
                    format: { with: VALID_EMAIL_REGEX },
                    uniqueness: { case_sensitive: false }
  validates :password, length: { minimum: 6 }

  ##################################################
  # Callbacks
  ##################################################
  before_save :downcase_email

  def downcase_email
    self.email = email.downcase
  end

  ##################################################
  # Methods
  ##################################################
  # Return hash digest of string
  def User.digest(string)
    cost = ActiveModel::SecurePassword.min_cost ? BCrypt::Engine::MIN_COST : BCrypt:: Engine.cost
    BCrypt::Password.create(string, cost:cost)
  end

  # Return random auth token for user
  def User.new_token
    # SecureRandom.urlsafe_base64
    return ENV['AUTH_TOKEN'] # temp to allow multiple logins of same user
  end

  # Stores hash of auth token for future sessions
  def remember
    self.auth_token = User.new_token
    update_attribute(:auth_digest, User.digest(auth_token))
    self.auth_token
  end

  def authenticated?(auth_token)
    # When we end sessions, we destroy the auth token, so nil tokens should always fail
    return false if auth_digest.nil?
    return false if auth_token.nil?
    BCrypt::Password.new(auth_digest).is_password?(auth_token)
  end

  def end_session
    update_attribute(:auth_digest, nil)
  end

end

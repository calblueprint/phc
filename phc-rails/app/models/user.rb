class User < ActiveRecord::Base
  attr_accessor :auth_token

  before_save { self.email = email.downcase }
  VALID_EMAIL_REGEX = /\A[\w+\-.]+@[a-z\d\-.]+\.[a-z]+\z/i

  validates :name, presence: true, length: { maximum: 50 }
  validates :email, presence: true, length: { maximum: 225 },
                                    format: { with: VALID_EMAIL_REGEX },
                                    uniqueness: { case_sensitive: false }

  has_secure_password
  validates :password, length: { minimum: 6 }

  # Return hash digest of string
  def User.digest(string)
    cost = ActiveModel::SecurePassword.min_cost ? BCrypt::Engine::MIN_COST : BCrypt:: Engine.cost
    BCrypt::Password.create(string, cost:cost)
  end

  # Return random auth token for user
  def User.new_token
    SecureRandom.urlsafe_base64
  end

  # Stores hash of auth token for future sessions
  def remember
    self.auth_token = User.new_token
    update_attribute(:auth_digest, User.digest(auth_token))
  end

  def authenticated?(auth_token)
    BCrypt::Password.new(auth_digest).is_password?(auth_token)
  end

end
